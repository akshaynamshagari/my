import React from 'react'
 
const ValidationMetadata = ({ validationMetadata }) => {
  return (
    <div>
      <h2>Validation Metadata</h2>
      <p><strong>Validation ID:</strong> {validationMetadata.validation_id}</p>
      <p><strong>Description:</strong> {validationMetadata.description}</p>
      <p><strong>Category:</strong> {validationMetadata.category}</p>
      <p><strong>Sub-Category:</strong> {validationMetadata.sub_category}</p>
      <p><strong>Severity:</strong> {validationMetadata.severity}</p>
      <p><strong>Source Component:</strong> {validationMetadata.source_component}</p>
      <p><strong>Ops Actionable:</strong> {validationMetadata.ops_actionable ? 'Yes' : 'No'}</p>
      <p><strong>Client Visible:</strong> {validationMetadata.client_visible ? 'Yes' : 'No'}</p>
      <p><strong>Threshold Enabled:</strong> {validationMetadata.threshold_enabled ? 'Yes' : 'No'}</p>
      <p><strong>Threshold Value:</strong> {validationMetadata.threshold_value}</p>
    </div>
  )
}
 
export default ValidationMetadata