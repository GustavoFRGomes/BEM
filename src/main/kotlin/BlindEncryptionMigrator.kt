import java.security.MessageDigest
import kotlin.collections.HashSet

class BlindEncryptionMigrator(
    private val storageManager: IStorageManager,
    private val statsBuilder: IStatsAggregator? = null
) {

    private val hashingSet: HashSet<String> by lazy {
        storageManager.readHashSet()
    }
    fun addEncryptedHash(hash: String) = hashingSet.add(hash).also {
        statsBuilder?.countNewHashAdded()
        persistData()
    }

    fun searchEncryptedHash(hash: String) = hashingSet.contains(hash).also {
        statsBuilder?.countHashHit(it)
    }

    private fun persistData() = storageManager.storeHashSet(hashingSet)

    companion object {
        @JvmStatic
        @JvmOverloads
        fun calculateHash(text: String, numberOfChars: Int? = 7): String {
            val digest = MessageDigest.getInstance("SHA-1")
            val hash = digest.digest(text.toByteArray())

            val hexOfHash = byteArrayToHexString(hash)
            val hashStringLength = numberOfChars ?: hexOfHash.length

            return hexOfHash.substring(0, hashStringLength)
        }

        @JvmStatic
        private fun byteArrayToHexString(byteArray: ByteArray): String {
            return byteArray.joinToString("") { "%02x".format(it) }
        }
    }
}