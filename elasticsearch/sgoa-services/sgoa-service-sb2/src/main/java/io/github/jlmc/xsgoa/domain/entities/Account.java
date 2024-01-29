package io.github.jlmc.xsgoa.domain.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.OffsetDateTime;


@Data
@Builder(toBuilder = true)
public class Account implements Serializable {

    @Field(type = FieldType.Text, name = "number")
    private String number;

    @Field(type = FieldType.Text, name = "full_number")
    private String fullNumber;

    @Field(type = FieldType.Text, name = "iban")
    private String iban;

    @Field(type = FieldType.Text, name = "nib")
    private String nib;

    @Field(type = FieldType.Text, name = "country_code")
    private String countryCode;

    @Field(type = FieldType.Text, name = "bank_code")
    private String bankCode;

    @Field(type = FieldType.Text, name = "type_code")
    private String typeCode;

    @Field(type = FieldType.Text, name = "currency_code")
    private String currencyCode;

    @Field(type = FieldType.Short, name = "deposit_sequential_number")
    private Short depositSequentialNumber;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time, name = "due_date")
    private OffsetDateTime dueDate;

    @Field(type = FieldType.Boolean, name = "renewal")
    private Boolean renewal;

}
