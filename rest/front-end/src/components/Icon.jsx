import React from 'react'

// Bootstrap Icons (https://icons.getbootstrap.com): <Icon name='gear-fill' /> renders <i class="bi bi-gear-fill">.
// Decorative by default: the text next to the icon carries the meaning, so screen readers skip it.
const Icon = ({ name, flip = false, label }) => (
  <i
    className={`bi bi-${name}${flip ? ' icon-flip-horizontal' : ''}`}
    aria-hidden={label ? undefined : 'true'}
    aria-label={label}
    role={label ? 'img' : undefined}
  />
)

export default Icon
