import data.HandleDataResource
import data.IResource
import pool.DataResourcePool
import pool.ObjectPool

lateinit var pool : ObjectPool

fun main() {
    setup()
    testPool()
}

fun setup() {
    pool = DataResourcePool(3)
}

fun testPool() {
    println("try pull object")
    var obj0 : HandleDataResource? = pool.pull()
    println("pulled object: ${obj0?.dataResource}")
    obj0?.process()

    Thread.sleep(4000)

    obj0 = null
    // garbage collect obj0
    System.gc()
}