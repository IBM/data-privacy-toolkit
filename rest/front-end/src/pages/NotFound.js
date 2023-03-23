import React from 'react'

import { withRouter } from 'react-router'
import { Link } from 'react-router-dom'

const NotFound = () => (<div>
  <h1>This is not the page you were looking for.</h1>
  <p>
  Page not found.
  If you reached this page selecting a feature from the demo menu then the feature is not available yet.
  Please come back later.
  </p>
  <p>
  Please, go back to <Link to='/'>main page</Link>!
  </p>
</div>)

export default withRouter(NotFound)
