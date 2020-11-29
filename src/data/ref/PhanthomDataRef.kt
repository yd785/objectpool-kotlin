package data.ref

import data.DataResource
import data.HandleDataResource
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

class PhanthomDataRef (referant : HandleDataResource, queue : ReferenceQueue<HandleDataResource>)
    : PhantomReference<HandleDataResource>(referant, queue) {

    var dataResource = referant.dataResource
        private set

    fun dispose() {
        val r = dataResource
        println("dispose PhantomResourceRef res " + r)
        r.release()
    }
}