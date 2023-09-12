package cc.unitmesh.cf.core.flow

import cc.unitmesh.cf.core.flow.model.ChatWebContext
import cc.unitmesh.cf.core.flow.model.StageContext
import cc.unitmesh.cf.core.flow.model.WorkflowResult
import io.reactivex.rxjava3.core.Flowable

abstract class Workflow {
    val chatWebContext: ChatWebContext? = null

    /**
     * save prompt list for debug in GUI
     */
    open val prompts: LinkedHashMap<StageContext.Stage, StageContext> = linkedMapOf()

    abstract fun execute(prompt: StageContext, chatWebContext: ChatWebContext): Flowable<WorkflowResult>
}