import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test

class BlindEncryptionMigrationTest {

    private val fakeStoredHashes = HashSet<String>().apply {
        add("234083y")
        add("2340734")
    }
    private val storageManagerMock = mockk<IStorageManager>(relaxed = true)
    private val statsAggregatorMock = mockk<IStatsAggregator>(relaxed = true)

    private val tested: BlindEncryptionMigrator = BlindEncryptionMigrator(storageManagerMock, statsAggregatorMock)

    @BeforeTest
    fun setup() {
        every { storageManagerMock.readHashSet() } returns fakeStoredHashes
    }


    @Test
    fun `on instantiating BlindEncryptionMigration it should retrieve from storage`() {
        tested.addEncryptedHash("tewfewatrew") // Need to perform some action since the first read is lazy

        verify(exactly = 1) { storageManagerMock.readHashSet() }
    }

    @Test
    fun `addEncryptedHash should add the hash to the store`() {
        tested.addEncryptedHash("test hash")

        verify(exactly = 1) { storageManagerMock.storeHashSet(fakeStoredHashes) }
    }

    @Test
    fun `addEncryptedHash and then searchEncryptedHash should be be true`() {
        val fakeHash = "test"
        tested.addEncryptedHash(fakeHash)

        verify(exactly = 1) { storageManagerMock.storeHashSet(fakeStoredHashes) }
        assert(tested.searchEncryptedHash(fakeHash))
    }

    @Test
    fun `searchEncryptedHash for a non existent hash should return false`() {
        val nonExistentHash = "qewrty"

        assert(!tested.searchEncryptedHash(nonExistentHash))
    }

    @Test
    fun `StatsAggregator countNewHashAdded should be called when adding new hash`() {
        `addEncryptedHash should add the hash to the store`()

        verify(exactly = 1) { statsAggregatorMock.countNewHashAdded() }
    }

    @Test
    fun `StatsAggregator countHashHit should be called with true for an existing stored hash`() {
        `addEncryptedHash and then searchEncryptedHash should be be true`()

        verify(exactly = 1) { statsAggregatorMock.countHashHit(true) }
    }

    @Test
    fun `StatsAggregator countHashHit should be called with false for a non existent hash`() {
        `searchEncryptedHash for a non existent hash should return false`()

        verify(exactly = 1) { statsAggregatorMock.countHashHit(false) }
    }
}