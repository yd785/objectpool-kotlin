package data.ref

import data.DataResource
import data.HandleDataResource
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

class PhanthomDataRef (referant : HandleDataResource, queue : ReferenceQueue<HandleDataResource>)
    : PhantomReference<HandleDataResource>(referant, queue) {

    private val dataResource = referant.dataResource

    fun dispose() {
        val r = dataResource
        println("dispose PhantomResourceRef")
        println("dispose PhantomResourceRef res " + r);
        r.release()
    }
}