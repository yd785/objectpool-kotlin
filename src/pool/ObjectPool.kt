package pool

import data.DataResource
import data.HandleDataResource
import data.IResource
import data.ref.PhanthomDataRef
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * demonstrate object pool design pattern
 * pull object from pool and return it once it is not used
 */
abstract class ObjectPool(val size: Int) : IPool {

    val freeObjPool: ConcurrentLinkedQueue<DataResource> by lazy { ConcurrentLinkedQueue<DataResource>() }
    val usedObjPool: ConcurrentLinkedQueue<DataResource> by lazy { ConcurrentLinkedQueue<DataResource>() }
    private val referenceQueue: ResourceReferenceQueue by lazy { ResourceReferenceQueue() }

    init {
        initialize()
    }

    override fun pull(): HandleDataResource {
        val resource = freeObjPool.poll() ?: createObject()
        usedObjPool.add(resource)
        println("pull - after pull size pool: ${freeObjPool.size}")
        println("pull - after pull uses size pool: ${usedObjPool.size}")
        val handleRes = HandleDataResource(resource)
        val ref = PhanthomDataRef(handleRes, referenceQueue)
        resource.setHandleRef(ref)

        return handleRes
    }

    abstract fun createObject(): DataResource

    private fun initialize() {
        for (i in 0 until size) {
            freeObjPool.add(createObject())
        }

        println("initialize size pool " + freeObjPool.size)
        println("initialize size pool uses " + usedObjPool.size)

        // start daemon thread
        thread(start = true, isDaemon = true) {
            while (true) {
                try {
                    println("run thread")
                    val ref: PhanthomDataRef = referenceQueue.remove() as PhanthomDataRef
                    println("run thread removed ref $ref")
                    ref.dispose()
                    val resource = ref.dataResource

                    // here add and return the object back to the pool
                    freeObjPool.add(resource)
                    usedObjPool.remove(resource)

                    println("size pool: ${freeObjPool.size}")
                    println("size pool uses: ${usedObjPool.size}")

                    ref.clear()
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }

    class ResourceReferenceQueue : ReferenceQueue<HandleDataResource>() {}


}

class DataResourcePool(size: Int) : ObjectPool(size) {
   var id : Int = 0

    override fun createObject(): DataResource {
        return DataResource(++id, "Name$id")
    }

}