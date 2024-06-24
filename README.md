# Import Voluntary Disclosure Submission

## Purpose
This is the backend service for the IVD frontend. It provides endpoints to the downstream services that call out the off platform.

Other related C18 services:
- Frontend service: [Import-Voluntary-Disclosure-Frontend](https://github.com/hmrc/import-voluntary-disclosure-frontend)
- Stub: [Import-Voluntary-Disclosure-Stub](https://github.com/hmrc/import-voluntary-disclosure-stub)

## Running the service
### Service manager
`sm2 --start IMPORT_VOLUNTARY_DISCLOSURE_SUBMISSION`
### Locally
`sbt run` or `sbt 'run 7951'`

## Endpoints
The following endpoints are implemented:


* **GET         /eoriDetails**        
  endpoint for retrieving Known EORI Details (makes downstream call to MDG SUB09)

* **POST        /case**              
  endpoint for creating a new submission (make downstream call to EIS Create case)

* **POST        /update-case**              
  endpoint for creating a new submission (make downstream call to EIS Update case)

## Downstream services
The following downstream services are used:

* **File Transmission Synchronous**
  used to make the transfer of the user supporting documentation files, through EIS to Documentum

* **EIS MDG**
  used for access to MDG SUB09 to retrieve the known EORI details

* **EIS CPR**
  used to submit new and updated cases through to the Pega CMS service

### Scalafmt
This repository uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf).

To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

 ```
 sbt scalafmtAll
 ```

To check files have been formatted as expected execute:

 ```
 sbt scalafmtCheckAll scalafmtSbtCheck
 ```