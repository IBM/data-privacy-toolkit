import React, { useCallback } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router'

// react-router 6 removed withRouter: class components get the router through these props instead
//   navigate(to) / navigate(-1)   replace history.push(to) / history.goBack()
//   location                      the current location
//   params                        the parameters of the matched route
const withRouter = Component => {
  const WithRouter = props => {
    const routerNavigate = useNavigate()
    // The pages call navigate(-1) from componentDidMount when they have no data. A child's componentDidMount runs
    // before the layout effect of this wrapper's useNavigate() that activates navigate(), so react-router would
    // drop the call and only log a warning. Deferring it to a microtask runs it right after the commit.
    const navigate = useCallback((...args) => queueMicrotask(() => routerNavigate(...args)), [routerNavigate])
    const location = useLocation()
    const params = useParams()

    return <Component {...props} navigate={navigate} location={location} params={params} />
  }

  WithRouter.displayName = `withRouter(${Component.displayName || Component.name})`
  return WithRouter
}

export default withRouter
