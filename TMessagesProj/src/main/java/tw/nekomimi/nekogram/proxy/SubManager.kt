package tw.nekomimi.nekogram.proxy

import org.dizitart.no2.filters.FluentFilter
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.database.mkDatabase

object SubManager {

    val database by lazy { mkDatabase("proxy_sub") }

    const val publicProxySubID = 1L

    @JvmStatic
    val count
        get() = subList.find().count()

    @JvmStatic
    val subList by lazy {

        database.getRepository<SubInfo>(SubInfo::class.java, "proxy_sub").apply {

            // val public = find(ObjectFilters.eq("id", publicProxySubID)).firstOrDefault()
            val public = find(FluentFilter.where("id").eq(publicProxySubID)).firstOrNull()

            val result = update(SubInfo().apply {
                // SubManager.kt -> SubInfo.java -> ProxyLoads.kt

                name = LocaleController.getString("NekoXProxy", R.string.NekoXProxy)
                enable = public?.enable ?: true

                urls = listOf(
                        "https://raw.githubusercontent.com/LiuYi0526/ProxyList/master/proxy_list_pro",  // Note: NO DoH apply to here and neko.services now.
                        "https://cdn.jsdelivr.net/gh/LiuYi0526/ProxyList@master/proxy_list_pro",
                )

                id = publicProxySubID
                internal = true

                proxies = public?.proxies ?: listOf()

            }, true)

        }

    }

}