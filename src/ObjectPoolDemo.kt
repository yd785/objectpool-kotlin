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
    var obj1 : HandleDataResource? = pool.pull()
    println("pulled object: ${obj1?.dataResource}")
    obj1?.process()

    Thread.sleep(4000)

    obj1 = null
    // garbage collect obj0
    System.gc()
}