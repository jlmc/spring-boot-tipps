package io.costax.examplesapi.tennacy;

import java.io.Serializable;

public interface TenancyMigration {
    void execute(Serializable tenantIdentifier);
}
