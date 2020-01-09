package com.silent_manager.g29.silent_manager.data_layer.models.dtos

interface IDto<T,D>{
    fun transformModelToDto(model: T): D
    fun transformDtoToModel(dto: D): T

}