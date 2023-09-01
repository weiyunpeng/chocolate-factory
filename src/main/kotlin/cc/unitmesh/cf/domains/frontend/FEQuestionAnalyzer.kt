package cc.unitmesh.cf.domains.frontend

import cc.unitmesh.cf.core.process.QuestionAnalyzer
import cc.unitmesh.cf.domains.frontend.dsl.FEDslContextBuilder
import cc.unitmesh.cf.domains.frontend.model.PageDsl
import cc.unitmesh.cf.infrastructure.llms.completion.CompletionProvider

class FEQuestionAnalyzer(
    private val contextBuilder: FEDslContextBuilder,
    private val completion: CompletionProvider,
) : QuestionAnalyzer {
    override fun analyze(domain: String, question: String): PageDsl {
        TODO()
    }
}