package tw.nekomimi.nekogram.proxy;

import androidx.annotation.NonNull;

import org.dizitart.no2.repository.annotations.Id;
import org.dizitart.no2.repository.annotations.Index;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.parts.ProxyLoadsKt;

@Index(fields = "id")
public class SubInfo {

    @Id
    public Long id;
    public String name;
    public List<String> urls = new LinkedList<>();
    public List<String> proxies = new LinkedList<>();
    public Long lastFetch = -1L;
    public boolean enable = true;
    public boolean internal;

    public SubInfo() {
    }

    public SubInfo(Long id, String name, List<String> urls, List<String> proxies, Long lastFetch, boolean enable, boolean internal) {
        this.id = id;
        this.name = name;
        this.urls = urls;
        this.proxies = proxies;
        this.lastFetch = lastFetch;
        this.enable = enable;
        this.internal = internal;
    }

    public String displayName() {

        if (id == SubManager.publicProxySubID)
            return LocaleController.getString("PublicPrefix", R.string.PublicPrefix);

        if (name.length() < 10) return name;

        return name.substring(0, 10) + "...";
    }

    public List<String> reloadProxies() throws IOException {

        HashMap<String, Exception> exceptions = new HashMap<>();

        try {
            if (id == SubManager.publicProxySubID) {
                if (!NekoConfig.enablePublicProxy.Bool())
                    return new ArrayList<>();
                List<String> pubs = ProxyLoadsKt.loadProxiesPublic(urls, exceptions);
                if (!NekoConfig.enablePublicProxy.Bool())
                    return new ArrayList<>();
                else
                    return pubs;
            } else {
                return ProxyLoadsKt.loadProxies(urls, exceptions);
            }
//            return id == SubManager.publicProxySubID ?  :
        } catch (Exception ignored) {
        }

        throw new SubInfo.AllTriesFailed(exceptions);

    }

    public static class AllTriesFailed extends IOException {

        public AllTriesFailed(HashMap<String, Exception> exceptions) {
            this.exceptions = exceptions;
        }

        public HashMap<String, Exception> exceptions;

        @NonNull
        @Override
        public String toString() {

            StringBuilder errors = new StringBuilder();

            for (Map.Entry<String, Exception> e : exceptions.entrySet()) {

                errors.append(e.getKey()).append(": ");

                errors.append(e.getValue().getClass().getSimpleName());

                if (!StrUtil.isBlank(e.getValue().getMessage())) {

                    errors.append(" ( ");
                    errors.append(e.getValue().getMessage());
                    errors.append(" )");

                }

                errors.append("\n\n");

            }

            return errors.toString();

        }

    }
}
