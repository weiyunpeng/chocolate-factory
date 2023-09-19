package cc.unitmesh.document.parser;

import cc.unitmesh.rag.document.DocumentType
import org.junit.jupiter.api.Test

class MsOfficeDocumentParserTest {
    @Test
    fun should_parse_pptx_file() {
        val inputStream = this.javaClass.getResourceAsStream("/sample.pptx")!!
        val documents = MsOfficeDocumentParser(DocumentType.PPT).parse(inputStream)

        assert(documents[0].text.contains("代码库问答"))
        assert(documents[1].text.contains("智能数据问答场景"))
    }
}