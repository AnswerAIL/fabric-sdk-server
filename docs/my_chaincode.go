package main

/*
	smart contract for myqkl
*/
import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
    "time"
    log "github.com/hyperledger/fabric/common/flogging"
	"encoding/json"
)

type Mycc struct {
}

type Pair struct {
	Key string `json:"key,omitempty"`
	Value string `json:"value,omitempty"`
}

type Record struct {
	TxId string 		`json:"txId,omitempty"`
	Value string 		`json:"value,omitempty"`
	Timestamp string 	`json:"timestamp,omitempty"`
	Delete bool 		`json:"delete"`
}

type Add struct {
	Key string		`json:"key,omitempty"`
	Newer bool		`json:"newer"`
	Success bool	`json:"success"`
}

type AddResult struct {
	BaseResult
	Data []Add		`json:"data,omitempty"`
}

type QueryResult struct {
	BaseResult
	Data Pair		`json:"data,omitempty"`
}

type BatchQueryResult struct {
	BaseResult
	Data []Pair		`json:"data,omitempty"`
}

type QueryHisResult struct {
	BaseResult
	Data []Record		`json:"data,omitempty"`
}

type BaseResult struct {
	TxId string 	`json:"txId"`
	Code int16		`json:"code"`
	Msg string		`json:"message"`
}

const (
	Create = "create"
	Read = "read"
	BatchRead = "batchRead"
	QueryHistoryForKey = "queryHistoryForKey"
	DT_FORMAT = "2006-01-02 15:04:05.000"
	QKL_NAME = "MYQKL"
	SUCCESS = 200
	FAILED = 999
)

var logger = log.MustGetLogger(QKL_NAME)



/*
	链码初始化入口
*/
func (t *Mycc) Init(stub shim.ChaincodeStubInterface) pb.Response {
	logger.Infof("chain code Init...\n")

	_, args := stub.GetFunctionAndParameters()

	// 参数个数必须是2的倍数
	if len(args) % 2 != 0 {
		return shim.Success([]byte("params's length must be a multiple of 2."))
	}
	for i := 0; i < len(args); i=i+2  {
		err := stub.PutState(args[i], []byte(args[i+1]))

		if err != nil {
			return shim.Error(err.Error())
		}
		logger.Infof("key: %s value: [%s] has been initialized successful.\n", args[i], args[i+1])
	}

	return shim.Success(nil)

}



/*
	SDK接口调用入口
*/
func (t *Mycc) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	txId := stub.GetTxID()
	logger.Infof("%s chain code Invoke...\n", txId)
	function, args := stub.GetFunctionAndParameters()

	switch function {
	case Create:
		return t.create(stub, args)
	case Read:
		return t.read(stub, args)
	case BatchRead:
		return t.batchRead(stub, args)
	case QueryHistoryForKey:
		return t.queryHistoryForKey(stub, args)
	default:
		return shim.Error("Invalid invoke function name. Excepting: " +
			"\"create\" \"read\" \"batchRead\" \"queryHistoryForKey\"")
	}
}



/*
	数据上链接口
*/
func (t *Mycc) create(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	txId := stub.GetTxID()
	logger.Infof("%s chain code Invoke Create\n", txId)

	var argsLen = len(args)

	if argsLen % 2 != 0 {
		jsonBytes, _ := json.Marshal(
			AddResult{
				BaseResult: BaseResult{txId, FAILED, "Incorrect number of arguments, Excepting multiple of 2."},
			})
		return shim.Success(jsonBytes)
	}

	var adds []Add

	for i := 0; i < argsLen; i=i+2  {
		key, value := args[i], args[i + 1]
		add := Add{key, true, false}

		// 判断 key是否已经存在
		OutAmtBytes, err := stub.GetState(key)
		if err != nil {
			logger.Errorf("%s key=%s, get key error=%s.\n", txId, key, err.Error())
			adds = append(adds, add)
			continue
		}

		// 记录覆盖前的数据信息
		if OutAmtBytes != nil {
			var oldValue = string(OutAmtBytes)
			logger.Infof("%s key=%s, old value=%s\n",
				txId, key, oldValue)
			add.Newer = false
		}

		logger.Infof("%s key: %s, value: %s\n", txId, key, value)
		err = stub.PutState(key, []byte(value))
		if err != nil {
			logger.Errorf("%s key=%s, put key error=%s.\n", txId, key, err.Error())
			adds = append(adds, add)
			continue
		}
		add.Success = true
		adds = append(adds, add)
	}

	result := AddResult{BaseResult{txId, SUCCESS, "success"}, adds}
	jsonBytes, _ := json.Marshal(result)
	logger.Infof("%s create result=%s\n", txId, string(jsonBytes))

	return shim.Success(jsonBytes)
}

/*
	批量查询接口
*/
func (t *Mycc) batchRead(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	txId := stub.GetTxID()
	logger.Infof("%s chain code BatchRead\n", txId)
	if len(args) < 1 {
		jsonBytes, _ := json.Marshal(
			BatchQueryResult{
				BaseResult: BaseResult{txId, FAILED, "Incorrect number of arguments, Expecting gt 1."},
			})
		return shim.Success(jsonBytes)
	}
	var result []Pair

	for _, key := range args {
		value, err := stub.GetState(key)
		if err != nil {
			logger.Warnf("%s query key=%s catch error\n", txId, key)
			continue
		}

		if value == nil {
			logger.Infof("%s query key=%s is not exists\n", txId, key)
			continue
		}

		logger.Infof("%s key=%s, value=%s\n", txId, key, string(value))
		pair := Pair{key, string(value)}
		result = append(result, pair)
	}
	jsonBytes, _ := json.Marshal(
		BatchQueryResult{BaseResult{txId, SUCCESS, "success"}, result})
	logger.Infof("%s batchQuery result=%s\n", txId, string(jsonBytes))

	return shim.Success(jsonBytes)
}

/*
	K-V查询
*/
func (t *Mycc) read(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	txId := stub.GetTxID()
	logger.Infof("%s chain code Read\n", txId)

	if len(args) != 1 {
		var response = QueryResult {
			BaseResult: BaseResult{txId, FAILED, "Incorrect number of arguments, Expecting 1."},
		}

		jsonBytes, _ := json.Marshal(response)
		return shim.Success(jsonBytes)
	}

	var key string
	// 获取要查询的 key
	key = args[0]
	value, err := stub.GetState(key)

	if err != nil {
		errorResp := "{\"key\": \""+ key +"\", \"value\": \"Failed to get key state(key is not exists).\"}"
		logger.Errorf("%s %s\n", txId, errorResp)
		var response = QueryResult {
			BaseResult: BaseResult{txId, FAILED, err.Error()},
		}
		jsonBytes, _ := json.Marshal(response)
		return shim.Success(jsonBytes)
	}
	if value == nil {
		jsonResp := "{\"key\": \""+ key +"\", \"value\": \"key is not exists.\"}"
		logger.Infof("%s %s\n", txId, jsonResp)
		var response = QueryResult {
			BaseResult: BaseResult{txId, SUCCESS, "key is not exists."},
		}
		jsonBytes, _ := json.Marshal(response)
		return shim.Success(jsonBytes)
	}

	var response = QueryResult {
		BaseResult: BaseResult{txId, SUCCESS, "success"},
		Data: Pair{Key: key, Value: string(value)},
	}
	jsonBytes, _ := json.Marshal(response)
	logger.Infof("%s jsonResp: %s\n", txId, string(jsonBytes))
	return shim.Success(jsonBytes)
}




/**
	查询键对应的历史(Key追溯查询)
*/
func (t *Mycc) queryHistoryForKey(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	location, _  := time.LoadLocation("Asia/Chongqing")
	txId := stub.GetTxID()
	logger.Infof("%s chain code QueryHistoryForKey\n", txId)
	if len(args) != 1 {
		jsonBytes, _ := json.Marshal(
			QueryHisResult{
				BaseResult: BaseResult{txId, FAILED, "Incorrect number of arguments, Expecting 1."},
			})
		return shim.Success(jsonBytes)
	}

	key := args[0]
	logger.Infof("%s query key: %s\n", txId, key)
	resultsIterator, err := stub.GetHistoryForKey(key)

	if err != nil {
		errorResp := "{\"key\": \""+ key +"\", \"value\": \""+ err.Error() +"\"}"
		logger.Errorf("%s %s\n", txId, errorResp)
		jsonBytes, _ := json.Marshal(
			QueryHisResult{
				BaseResult: BaseResult{txId, FAILED, err.Error()},
			})
		return shim.Success(jsonBytes)
	}

	defer resultsIterator.Close()

	var result []Record
	// 没有查询到历史记录, 即: 当前key无操作记录
	if !resultsIterator.HasNext() {
		noneResp := "{\"key\": \""+ key +"\", \"value\": \"The query result is empty\"}"
		logger.Infof("%s %s\n", txId, noneResp)
		jsonBytes, _ := json.Marshal(
			QueryHisResult{
				BaseResult: BaseResult{txId, SUCCESS, "query result is empty."},
			})
		return shim.Success(jsonBytes)
	}

	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			logger.Errorf("%s key=%s error=%s\n", txId, key, err.Error())
			jsonBytes, _ := json.Marshal(
				QueryHisResult{
					BaseResult: BaseResult{txId, FAILED, err.Error()},
				})
			return shim.Success(jsonBytes)
		}

		record := Record{
			response.TxId,
			string(response.Value),
			time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).In(location).Format(DT_FORMAT),
			response.IsDelete,
		}
		result = append(result, record)
	}
	jsonBytes, _ := json.Marshal(
		QueryHisResult{BaseResult{txId, SUCCESS, "success"}, result})

	logger.Infof("%s key: %s, history response: %s\n", txId, key, string(jsonBytes))
	return shim.Success(jsonBytes)
}


func main() {
	logger.Infof("startup smart contract...\n")
	err := shim.Start(new(Mycc))
	if err != nil {
		logger.Errorf("startup smart contract failed: %s\n", err)
	}
}
