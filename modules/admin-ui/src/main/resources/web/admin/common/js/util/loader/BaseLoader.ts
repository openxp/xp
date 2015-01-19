module api.util.loader {

    import LoaderEvents = api.util.loader.event.LoaderEvents;
    import LoaderEvent = api.util.loader.event.LoaderEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    enum LoaderStatus {
        LOADING,
        LOADED
    }

    export class BaseLoader<JSON, OBJECT> {

        private request: api.rest.ResourceRequest<JSON, OBJECT[]>;

        private status: LoaderStatus;

        private results: OBJECT[];

        private searchString: string;

        private loadedDataListeners: {(event: LoadedDataEvent<OBJECT>):void}[] = [];

        private loadingDataListeners: {(event: LoadingDataEvent):void}[] = [];

        constructor(request: api.rest.ResourceRequest<JSON, OBJECT[]>) {
            this.setRequest(request);
        }

        sendRequest(): wemQ.Promise<OBJECT[]> {
            return this.request.sendAndParse();
        }

        load(): void {
            this.notifyLoadingData();
            this.sendRequest().done((results: OBJECT[]) => {
                this.results = results;
                this.notifyLoadedData(results);
            });
        }

        isLoading(): boolean {
            return this.status == LoaderStatus.LOADING;
        }

        isLoaded(): boolean {
            return this.status == LoaderStatus.LOADED;
        }

        setRequest(request: api.rest.ResourceRequest<JSON, OBJECT[]>) {
            this.request = request;
        }

        getRequest(): api.rest.ResourceRequest<JSON, OBJECT[]> {
            return this.request;
        }

        search(searchString: string) {

            this.searchString = searchString;
            if (this.results) {
                var filtered = this.results.filter(this.filterFn, this);
                this.notifyLoadedData(filtered);
            }
        }

        getSearchString(): string {
            return this.searchString;
        }

        filterFn(result: OBJECT): boolean {
            throw Error("must be implemented");
        }

        notifyLoadedData(results: OBJECT[]) {
            this.status = LoaderStatus.LOADED;
            this.loadedDataListeners.forEach((listener: (event: LoadedDataEvent<OBJECT>)=>void)=> {
                listener.call(this, new LoadedDataEvent<OBJECT>(results));
            });
        }

        notifyLoadingData() {
            this.status = LoaderStatus.LOADING;
            this.loadingDataListeners.forEach((listener: (event: LoadingDataEvent)=>void)=> {
                listener.call(this, new LoadingDataEvent());
            });
        }

        onLoadedData(listener: (event: LoadedDataEvent<OBJECT>) => void) {
            this.loadedDataListeners.push(listener);
        }

        onLoadingData(listener: (event: LoadingDataEvent) => void) {
            this.loadingDataListeners.push(listener);
        }

        unLoadedData(listener: (event: LoadedDataEvent<OBJECT>) => void) {
            this.loadedDataListeners = this.loadedDataListeners.filter((currentListener: (event: LoadedDataEvent<OBJECT>)=>void)=> {
                return currentListener != listener;
            });
        }

        unLoadingData(listener: (event: LoadingDataEvent) => void) {
            this.loadingDataListeners = this.loadingDataListeners.filter((currentListener: (event: LoadingDataEvent)=>void)=> {
                return currentListener != listener;
            });
        }

    }
}