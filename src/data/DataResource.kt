package data

data class DataResource(val id : Int, val name : String) : IResource {

    init {
        println("Object with resource id. $id , name: $name was created")
    }

    override fun doSomthing() {
        println("$id start do heavy stuff");
        // do heavy stuff here
        System.out.println("$id finish do heavy stuff");
    }

    @Synchronized override fun release() {
        println("release $this")
    }
}
