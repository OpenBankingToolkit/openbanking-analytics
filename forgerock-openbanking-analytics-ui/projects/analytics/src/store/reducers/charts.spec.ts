import chartsReducer, {
  DEFAULT_STATE,
  GetChartRequestAction,
  GetChartSuccessAction,
  GetChartErrorAction,
  GetChartFinishedAction
} from './charts';

describe('chartsReducer', () => {
  it('should return the default state', () => {
    const action = {};
    const state = chartsReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('should return the default state if action type does not match', () => {
    const action = { type: 'whatever' };
    const state = chartsReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('GetChartRequestAction should set isFetching to true', () => {
    const id = 'tpp';
    let state = chartsReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = chartsReducer(state, new GetChartRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);
  });

  it('GetChartRequestAction should set isFetching to false and set config', () => {
    const id = 'tpp';
    const config = {
      type: 'test'
    };
    let state = chartsReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = chartsReducer(state, new GetChartRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);

    state = chartsReducer(state, new GetChartSuccessAction({ id, config }));

    expect(state[id]).toEqual({
      isFetching: false,
      config
    });
  });

  it('GetChartErrorAction should set isFetching to false', () => {
    const id = 'tpp';
    const error = 'this is an error';
    let state = chartsReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = chartsReducer(state, new GetChartRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);

    state = chartsReducer(state, new GetChartErrorAction({ id, error }));

    expect(state[id]).toEqual({
      isFetching: false,
      error
    });
  });

  it('GetChartFinishedAction should set isFetching to false', () => {
    const id = 'id1';
    let state = chartsReducer(DEFAULT_STATE, {});

    state = chartsReducer(state, new GetChartRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);

    state = chartsReducer(state, new GetChartFinishedAction({ id }));

    expect(state[id].isFetching).toEqual(false);
  });
});
