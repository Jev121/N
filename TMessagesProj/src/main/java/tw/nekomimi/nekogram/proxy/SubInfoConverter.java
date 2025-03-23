package tw.nekomimi.nekogram.proxy;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.EntityConverter;
import org.dizitart.no2.common.mapper.NitriteMapper;

import java.util.ArrayList;
import java.util.List;

public class SubInfoConverter implements EntityConverter<SubInfo> {
    @Override
    public Class<SubInfo> getEntityType() {
        return SubInfo.class;
    }

    @Override
    public Document toDocument(SubInfo entity, NitriteMapper nitriteMapper) {
        return Document.createDocument()
                .put("id", entity.id)
                .put("name", entity.name)
                .put("urls", entity.urls)
                .put("proxies", entity.proxies)
                .put("lastFetch", entity.lastFetch)
                .put("enable", entity.enable)
                .put("internal", entity.internal);
    }

    @Override
    public SubInfo fromDocument(Document document, NitriteMapper nitriteMapper) {
        SubInfo entity = new SubInfo();
        Long idValue = document.get("id", Long.class);
        if (idValue == null) {
            System.err.println("Warning: 'id' field is null or missing in SubInfo document. Setting default id value to -1. Document: " + document);
            entity.id = 1L; // 使用默认 id 值 1L，更符合通常的起始 ID 习惯
        } else {
            entity.id = idValue;
        }
        entity.name = document.get("name", String.class);

        // Convert urls List<String> from Document
        List<?> urlsList = document.get("urls", List.class);
        if (urlsList != null) {
            List<String> urlsStringList = new ArrayList<>();
            for (Object url : urlsList) {
                if (url instanceof String) {
                    urlsStringList.add((String) url);
                } else {
                    System.err.println("Warning: Non-String element found in 'urls' list of SubInfo: " + url);
                }
            }
            entity.urls = urlsStringList;
        }

        // Convert proxies List<String> from Document
        List<?> proxiesList = document.get("proxies", List.class);
        if (proxiesList != null) {
            List<String> proxiesStringList = new ArrayList<>();
            for (Object proxy : proxiesList) {
                if (proxy instanceof String) {
                    proxiesStringList.add((String) proxy);
                } else {
                    System.err.println("Warning: Non-String element found in 'proxies' list of SubInfo: " + proxy);
                }
            }
            entity.proxies = proxiesStringList;
        }

        // 处理 enable 字段可能为 null 的情况，如果为 null，则默认设置为 false
        Boolean enableValue = document.get("enable", Boolean.class);
        entity.enable = enableValue != null ? enableValue : false;

        // 处理 internal 字段可能为 null 的情况，如果为 null，则默认设置为 false
        Boolean internalValue = document.get("internal", Boolean.class);
        entity.internal = internalValue != null ? internalValue : false;

        entity.lastFetch = document.get("lastFetch", Long.class);
        return entity;
    }
}
