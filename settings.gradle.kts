rootProject.name = "ecommerce"

include(
    "module-app",
    "module-domain",
    "module-infra",
    "module-apis",
    "module-apis:customer-api",
    "module-apis:partner-api",
    "module-auth",
    "module-common"
)
