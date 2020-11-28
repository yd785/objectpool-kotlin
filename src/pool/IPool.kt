package pool

import data.IResource

interface IPool {
    fun pull() : IResource
}