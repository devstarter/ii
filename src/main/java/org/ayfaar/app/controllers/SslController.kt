package org.ayfaar.app.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SslController {

    @ResponseBody
    @RequestMapping(".well-known/acme-challenge/{id}")
    fun authenticate(@PathVariable id: String) = "$id.D9BNMjYsHutrxi_T8Ib1BDBt2R3kXzsnu2_S3tu6aTI" //"3MDRqPVXLHN63zsNO_obi0Ww9RzG3G-h5Q4YcY96f7M.D9BNMjYsHutrxi_T8Ib1BDBt2R3kXzsnu2_S3tu6aTI"
}
