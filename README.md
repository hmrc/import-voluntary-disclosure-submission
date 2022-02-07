
# import-voluntary-disclosure-submission

# Import Voluntary Disclosure Submission

## Purpose
This is the backend service for the IVD frontend. It provides endpoints to the downstream services that call out the off platform.

## Running the service
### Service manager
`sm --start IMPORT_VOLUNTARY_DISCLOSURE_SUBMISSION -r`
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