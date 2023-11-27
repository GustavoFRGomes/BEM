
interface IStorageManager {
    fun storeHashSet(hashes: HashSet<String>)

    fun readHashSet(): HashSet<String>
}