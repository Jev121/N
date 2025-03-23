package tw.nekomimi.nekogram.proxy

import org.dizitart.no2.common.mapper.SimpleNitriteMapper
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.filters.FluentFilter
import org.dizitart.no2.repository.ObjectRepository
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.database.mkDatabase

object SubManager {

    val database by lazy { mkDatabase("proxy_sub", module = NitriteModule.module(getNitriteMapper())) }

    const val publicProxySubID = 1L

    @JvmStatic fun getNitriteMapper(): SimpleNitriteMapper {
        val nitriteMapper = SimpleNitriteMapper()
        nitriteMapper.registerEntityConverter(SubInfoConverter())
        return nitriteMapper
    }

    @JvmStatic
    val count
        get() = subList.find().count()

    @JvmStatic
    val subList: ObjectRepository<SubInfo> by lazy {
        database.getRepository(SubInfo::class.java, "proxy_sub").apply {
            find(FluentFilter.where("id").eq(publicProxySubID)).firstOrNull()
            update(SubInfo().apply {
                // SubManager.kt -> SubInfo.java -> ProxyLoads.kt
                name = LocaleController.getString("NekoXProxy", R.string.NekoXProxy)
                enable = true
                urls = listOf(
                    "https://raw.githubusercontent.com/LiuYi0526/ProxyList/master/proxy_list_pro",  // Note: NO DoH apply to here and neko.services now.
                    "https://cdn.jsdelivr.net/gh/LiuYi0526/ProxyList@master/proxy_list_pro",)

                id = publicProxySubID
                internal = true
                proxies = listOf()
            }, true)
        }
    }
}