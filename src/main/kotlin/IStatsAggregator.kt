interface IStatsAggregator {
    fun countNewHashAdded()
    fun countHashHit(hashHit: Boolean)
}