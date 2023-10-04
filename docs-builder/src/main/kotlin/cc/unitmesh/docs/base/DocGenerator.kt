package cc.unitmesh.docs.base

import cc.unitmesh.docs.kdoc.KDocContent
import java.io.File

data class TreeDoc(
    val root: KDocContent,
    val children: List<KDocContent>,
)

abstract class DocGenerator {
    val baseDir = "build" + File.separator

    open fun execute() : List<TreeDoc> {
        return listOf()
    }
}