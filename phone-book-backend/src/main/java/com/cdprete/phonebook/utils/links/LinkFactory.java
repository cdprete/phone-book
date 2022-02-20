package com.cdprete.phonebook.utils.links;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.cdprete.phonebook.utils.links.Link.Type.first;
import static com.cdprete.phonebook.utils.links.Link.Type.last;
import static com.cdprete.phonebook.utils.links.Link.Type.next;
import static com.cdprete.phonebook.utils.links.Link.Type.previous;
import static java.lang.Math.max;

/**
 * @author Cosimo Damiano Prete
 * @since 05/02/2022
 */
@Component
public class LinkFactory {
    private final Pageable pageableConfigProperties;

    public LinkFactory(SpringDataWebProperties springDataWebProperties) {
        pageableConfigProperties = springDataWebProperties.getPageable();
    }

    public <T> Set<Link> fromPaginatedHttpRequest(@NotNull Page<@Valid T> pageResult, @NotNull HttpServletRequest request) {
        var baseUrl = request.getRequestURL();
        var pageSize = pageResult.getSize();
        var links = new LinkedHashSet<Link>();
        var firstPageIndex = pageableConfigProperties.isOneIndexedParameters() ? 1 : 0;
        // Pages are reported as 0-indexed even if the config is set to 1-indexed, so we just increment the value if needed.
        var currentPageIndex = pageResult.getNumber() + firstPageIndex;
        links.add(new Link(first, composeEntireUrl(baseUrl, firstPageIndex, pageSize)));
        if(!pageResult.isFirst()) {
            links.add(new Link(previous, composeEntireUrl(baseUrl, currentPageIndex - 1, pageSize)));
        }
        if(!pageResult.isLast()) {
            links.add(new Link(next, composeEntireUrl(baseUrl, currentPageIndex + 1, pageSize)));
        }
        links.add(new Link(last, composeEntireUrl(baseUrl, max(firstPageIndex, pageResult.getTotalPages()), pageSize)));

        return links;
    }

    private URI composeEntireUrl(CharSequence url, int page, int pageSize) {
        //noinspection StringBufferReplaceableByString
        var builder = new StringBuilder(url);
        builder.append('?');
        builder.append(pageableConfigProperties.getPageParameter());
        builder.append('=');
        builder.append(page);
        builder.append('&');
        builder.append(pageableConfigProperties.getSizeParameter());
        builder.append('=');
        builder.append(pageSize);

        return URI.create(builder.toString());
    }
}
