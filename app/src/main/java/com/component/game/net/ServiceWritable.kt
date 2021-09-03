package com.component.game.net


/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * @Author: pym
 * @Date: 2021/4/20 15:55
 * @Description:
 */
interface ServiceWritable {
    fun connect()
    fun reNet(isForce: Boolean)
    fun close()
    fun sendMsg(msg: String)
    fun getServiceBinder(): IMService.ServiceBinder


}