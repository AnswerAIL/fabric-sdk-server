package com.hyperledger.fabric.sdk.entity;

import lombok.Getter;

/**
 * Created by L.Answer on 2018-11-02 14:40
 */
public enum OptTypeEnum {
    /** 初始化 */
    INITIAL("init"),

    /** 增 */
    CREATE("create"),
    /** 删 */
    DELETE("delete"),
    /** 改 */
    MODIFY("update"),
    /** 查 */
    QUERY("read"),

    /** 富查询 */
    RICH_QUERY("richQuery"),
    /** 查询 key 的历史操作集 */
    QUERY_HISTORY_FOR_KEY("queryHistoryForKey"),
    /** 分页查询 */
    QUERY_WITH_PAGINATION("queryWithPagination"),
    /** 根据 key 范围查询 */
    QUERY_FOR_KEY_BY_RANGE("queryForKeyByRange"),
    /** 根据 key 范围查询并分页 */
    QUERY_FOR_KEY_BY_RANGE_WITH_PAGINATION("queryForKeyByRangeWithPagination");


    OptTypeEnum(String name) {
        this.name = name;
    }

    @Getter
    private String name;
}