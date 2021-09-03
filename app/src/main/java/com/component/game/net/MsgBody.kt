package com.component.game.net

/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * @Author: pym
 * @Date: 2021/9/3 10:15
 * @Description:
 */
class MsgBody(
    var preX: Float = 0f,
    var preY: Float = 0f,
    var x: Float = 0f,
    var y: Float = 0f
) {
    var userId = Client.getAccount()
}