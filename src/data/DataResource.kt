package data

import data.ref.PhanthomDataRef

data class DataResource(val id : Int, val name : String) : IResource {
    lateinit var handle : PhanthomDataRef

    init {
        println("Object with resource id. $id , name: $name was created")
    }

    fun setHandleRef(_handle: PhanthomDataRef) {
        handle = _handle
    }

    override fun doSomthing() {
        println("$id start do heavy stuff")
        // do heavy stuff here
        System.out.println("$id finish do heavy stuff")
    }

    @Synchronized override fun release() {
        println("release $this")
    }
}
