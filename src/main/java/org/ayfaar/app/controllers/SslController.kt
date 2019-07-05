package org.ayfaar.app.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SslController {

    @ResponseBody
    @RequestMapping(".well-known/acme-challenge/{id}")
    fun authenticate(@PathVariable id: String) = "$id.CzwDj3G70ZUXvo9joO4OeSfe01Iou6l1_RUhv4pc2mc"
}
