import pdfReducer, { DEFAULT_STATE, GetPdfRequestAction, GetPdfSuccessAction, GetPdfErrorAction } from './pdf';

describe('pdfReducer', () => {
  it('should return the default state', () => {
    const action = {};
    const state = pdfReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('should return the default state if action type does not match', () => {
    const action = { type: 'whatever' };
    const state = pdfReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('GetPdfRequestAction should set isFetching to true', () => {
    const id = 'tpp';
    let state = pdfReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = pdfReducer(state, new GetPdfRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);
  });

  it('GetPdfSuccessAction should set isFetching to false', () => {
    const id = 'tpp';
    let state = pdfReducer(DEFAULT_STATE, new GetPdfRequestAction({ id }));
    expect(state[id].isFetching).toEqual(true);

    state = pdfReducer(DEFAULT_STATE, new GetPdfSuccessAction({ id }));
    expect(state[id].isFetching).toEqual(false);
  });

  it('GetPdfErrorAction should set isFetching to false', () => {
    const id = 'tpp';
    let state = pdfReducer(DEFAULT_STATE, new GetPdfRequestAction({ id }));
    expect(state[id].isFetching).toEqual(true);

    state = pdfReducer(DEFAULT_STATE, new GetPdfErrorAction({ id }));
    expect(state[id].isFetching).toEqual(false);
  });
});
