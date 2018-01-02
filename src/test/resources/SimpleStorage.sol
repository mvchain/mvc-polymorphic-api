contract SimpleStorage {
    uint storedData;
    function set(uint x) {
        storedData = x;
    }
    function get() constant returns (uint retVal) {
        return {0};
    }

    function get1() constant returns (uint retVal) {
        return {1};
    }
}