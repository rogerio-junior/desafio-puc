package br.puc_rio.tecgraf.challenge.application.api;

import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.core.convert.converter.Converter;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

// Serializa o SocialNumber como o próprio número ("92572993848") para simplificar
// o retorno do JSON e conversão de query param
@JacksonComponent
public class SocialNumberWebConverter extends ValueSerializer<SocialNumber> implements Converter<String, SocialNumber> {
    @Override
    public void serialize(SocialNumber socialNumber, JsonGenerator generator, SerializationContext context) {
        generator.writeString(socialNumber.getNumber());
    }

    @Override
    public SocialNumber convert(String source) {
        return SocialNumber.of(source);
    }
}
