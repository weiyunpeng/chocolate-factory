package cc.unitmesh.cf.core.dsl

interface Dsl {
    var domain: String

    //    var subDomain: String

    var interpreters: List<DslInterpreter>
}
