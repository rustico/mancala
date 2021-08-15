package com.bol.api.controller

import com.bol.api.socket.Greeting
import com.bol.api.socket.HelloMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.HtmlUtils


@RestController
class SocketController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    @Throws(Exception::class)
    fun greeting(message: HelloMessage): Greeting {
        return Greeting("Hello, " + HtmlUtils.htmlEscape(message.name) + "!")
    }
}