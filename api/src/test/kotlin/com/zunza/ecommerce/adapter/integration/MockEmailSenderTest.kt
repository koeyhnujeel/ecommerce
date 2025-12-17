package com.zunza.ecommerce.adapter.integration

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.StdIo
import org.junitpioneer.jupiter.StdOut

class MockEmailSenderTest {
    @Test
    @StdIo
    fun send(out: StdOut) {
        val mockEmailSender = MockEmailSender()

        mockEmailSender.send("example@email.com", "title", "message")

        out.capturedLines()[0] shouldBe "[Mock] EmailSender Sending email: example@email.com"
    }
}