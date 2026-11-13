import configurations from './configurations'
import identifiers from './identifiers'
import maskingProviders from './maskingProviders'
import anonymizationProviders from './anonymizationProviders'
import informationLossMetrics from './informationLossMetrics'
import hierarchies from './hierarchies'
import workflow from './workflow'
import dicom from './dicom'
import freetext from './freetext'
import alerts from './alerts'

const reducers = {
  configurations,
  identifiers,
  maskingProviders,
  anonymizationProviders,
  informationLossMetrics,
  hierarchies,
  workflow,
  dicom,
  freetext,
  alerts
}

export default reducers
