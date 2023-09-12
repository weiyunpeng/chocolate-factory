package cc.unitmesh.cf.presentation.controller

import cc.unitmesh.cf.core.flow.model.ChatWebContext
import cc.unitmesh.cf.core.flow.model.Message
import cc.unitmesh.cf.core.flow.model.StageContext
import cc.unitmesh.cf.core.flow.model.WorkflowResult
import cc.unitmesh.cf.domains.SupportedDomains
import cc.unitmesh.cf.domains.code.CodeInterpreterWorkflow
import cc.unitmesh.cf.domains.frontend.FEWorkflow
import cc.unitmesh.cf.domains.testcase.TestcaseWorkflow
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@Controller
class ChatController(
    val feFlow: FEWorkflow,
    val codeFlow: CodeInterpreterWorkflow,
    val testcaseFlow: TestcaseWorkflow,
) {
    @PostMapping("/chat", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chat(@RequestBody chat: ChatRequest): SseEmitter {
        val emitter = SseEmitter()

        // 1. search by domains
        val workflow = when (chat.domain) {
            SupportedDomains.Frontend -> feFlow
            SupportedDomains.CodeInterpreter -> codeFlow
            SupportedDomains.Testcase -> testcaseFlow
            SupportedDomains.SQL -> TODO()
            SupportedDomains.Custom -> TODO()
        }

        // 2. searches by stage
        val prompt = workflow.prompts[chat.stage]
            ?: throw RuntimeException("prompt not found!")

        // 3. execute stage with prompt
        val chatWebContext = chat.toContext()

        val result = workflow.execute(prompt, chatWebContext)
        result.subscribe({
            log.info("workflow result: {}", it)
            try {
                emitter.send(MessageResponse.from(chat.id, it))
            } catch (e: IOException) {
                emitter.completeWithError(e)
            }
        }, {
            emitter.completeWithError(it)
        }, {
            try {
                emitter.complete()
            } catch (e: IOException) {
                // ignore
            }
        })

        return emitter
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatController::class.java)
    }
}

@Serializable
data class MessageResponse(
    val id: String,
    val result: WorkflowResult?,
    val created: Long = DateTime.now().millis,
    val isFlowable: Boolean = false,
    val messages: List<Message> = emptyList(),
) {
    companion object {
        fun from(id: String, result: WorkflowResult?): MessageResponse {
            val messages = listOf(Message("assistant", result?.responseMsg ?: ""))
            return MessageResponse(id, result, messages = messages, isFlowable = result?.isFlowable ?: false)
        }
    }
}

data class ChatRequest(
    val messages: List<Message>,
    val id: String,
    val stage: StageContext.Stage,
    val domain: SupportedDomains,
    val previewToken: String?,
) {
    fun toContext(): ChatWebContext {
        return ChatWebContext(
            messages = this.messages,
            id = this.id,
            stage = this.stage,
        )
    }
}
