package cn.anc.dashcam.core.data

import cn.anc.dashcam.core.model.BrandInfo

interface BrandRepository {
    fun currentBrand(): BrandInfo
}

class InMemoryBrandRepository(
    private val brandInfo: BrandInfo,
) : BrandRepository {
    override fun currentBrand(): BrandInfo = brandInfo
}
