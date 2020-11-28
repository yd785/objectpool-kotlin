package data

import pool.ObjectPool

class HandleDataResource (val dataResource: ObjectPool.RefDataResource) : IResource {

    fun process() = doSomthing()

    override fun release() = dataResource.release()

    override fun doSomthing() = dataResource.doSomthing()

}