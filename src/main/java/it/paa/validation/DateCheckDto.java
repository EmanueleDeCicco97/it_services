package it.paa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) //Indica che questa annotazione può essere applicata solo a elementi di tipo classe.

@Retention(RetentionPolicy.RUNTIME)
// Indica che questa annotazione deve essere conservata e resa disponibile in fase di runtime, in modo che
// possa essere utilizzata per la convalida dei dati a runtime.
@Constraint(validatedBy = DateCheckValidatorDto.class)
//Specifica la classe che implementa la logica di validazione per questa annotazione.


@Documented //Indica che questa annotazione dovrebbe essere inclusa nella documentazione generata automaticamente.
public @interface DateCheckDto {
    String message() default "The end date of the project must be later than the start date";

    //Specifica i gruppi di vincoli ai quali appartiene questa annotazione. I gruppi possono essere utilizzati per
    // abilitare o disabilitare intere suite di vincoli.
    Class<?>[] groups() default {};

    //Consente di allegare metadati arbitrari alla restrizione.
    //I payload sono principalmente utilizzati da framework di convalida avanzati.
    Class<? extends Payload>[] payload() default {};
}