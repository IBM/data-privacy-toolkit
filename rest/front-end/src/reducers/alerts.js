import { SHOW_ALERT, DISMISS_ALERT } from '../actions/alerts'

// The messages waiting to be read, oldest first. The same message is shown once, however often it is raised.
const reducer = (state = [], action) => {
  switch (action.type) {
    case SHOW_ALERT:
      return state.some(alert => alert.message === action.message)
        ? state
        : [...state, { id: action.id, message: action.message }]

    case DISMISS_ALERT:
      return state.filter(alert => alert.id !== action.id)

    default:
      return state
  }
}

export default reducer
