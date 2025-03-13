package com.ndm.serve.dtos.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequestDTO {
    String templateName;
    String to;
    String cc;
    String bcc;
    String subject;
    String body;
    Map<String, Object> variables;
}
