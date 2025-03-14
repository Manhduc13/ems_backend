package com.ndm.serve.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomPagedResponse<T> {
    Collection<T> data;

    PagedModel.PageMetadata page;

    Links links;
}
