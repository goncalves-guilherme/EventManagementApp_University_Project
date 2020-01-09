package com.silent_manager.g29.silent_manager.data_layer.models

import com.fasterxml.jackson.annotation.JsonProperty


class PageResult<T>(
    @JsonProperty("currentPage")
    val currentPage: Int?,
    @JsonProperty("pageCount")
    val pageCount: Int?,
    @JsonProperty("pageSize")
    val pageSize: Int?,
    @JsonProperty("results")
    val results: Array<T>?
)