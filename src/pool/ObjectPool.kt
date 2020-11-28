package pool

import data.DataResource
import data.HandleDataResource
import data.IResource
import data.ref.PhanthomDataRef
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread


abstract class ObjectPool(val size: Int) : IPool {

    val freeObjPool: ConcurrentLinkedQueue<RefDataResource> by lazy { ConcurrentLinkedQueue<RefDataResource>() }
    val usedObjPool: ConcurrentLinkedQueue<RefDataResource> by lazy { ConcurrentLinkedQueue<RefDataResource>() }
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

    abstract fun createObject(): RefDataResource

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
                    ref.clear()
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    }

    class RefDataResource(
        id: Int, name: String,
        _freeObjPool: ConcurrentLinkedQueue<RefDataResource>, _usedObjPool: ConcurrentLinkedQueue<RefDataResource>
    ) : IResource by DataResource(id, name) {
        var handle: PhanthomDataRef ? = null
        private val freeObjPool = _freeObjPool
        private val usedObjPool = _usedObjPool

        fun setHandleRef(handle: PhanthomDataRef) {
            this.handle = handle
        }

        @Synchronized
        override fun release() {
            handle?.clear()
            handle = null
            println("release: return object back to pool queue: $this");
            freeObjPool.add(this)
            usedObjPool.remove(this)

            println("size pool: ${freeObjPool.size}")
            println("size pool uses: ${usedObjPool.size}")
        }
    }

    class ResourceReferenceQueue : ReferenceQueue<HandleDataResource>() {}


}

class DataResourcePool(size: Int) : ObjectPool(size) {

    var id: Int = 0

    override fun createObject(): RefDataResource {
        return ObjectPool.RefDataResource(++id, "Name$id", freeObjPool, usedObjPool)
    }

}