package com.mvc.ethereum.rest.resources;

import com.cegeka.tetherj.NoSuchContractMethod;
import com.mvc.ethereum.exceptions.ExceededGasException;
import com.mvc.ethereum.model.Receipt;
import com.mvc.ethereum.rest.resources.sample.SampleVisitor;
import com.mvc.ethereum.rpc.services.BlockchainFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/contracts")
public class ContractResource<T> {

    @Autowired
    private BlockchainFacade facade;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody EthereumResponse< Map<Receipt.Type, Future<Receipt>>> createContracts(@RequestBody final SampleVisitor visitor) throws ExecutionException, InterruptedException {
        try {
            Future<Receipt> contract = facade.createContract(visitor);
            return new EthereumResponse(contract.get(), 200, "OK");

        } catch (ExceededGasException e) {
            return new EthereumResponse(null,400, e.getMessage());
        } catch (NoSuchContractMethod e) {
            return new EthereumResponse(null,404, e.getMessage());
        }
    }


    @RequestMapping(value = "/{contractAddress}", method = RequestMethod.PUT)
    public @ResponseBody EthereumResponse<Receipt> modifyContract(@PathVariable final String contractAddress, @RequestBody final SampleVisitor visitor) {
        try {
            return new EthereumResponse(facade.modifyContract(contractAddress, visitor),200, "OK");
        } catch (ExceededGasException e) {
            return new EthereumResponse(null,400, e.getMessage());
        } catch (NoSuchContractMethod e) {
            return new EthereumResponse(null,404, e.getMessage());
        }
    }

    @RequestMapping(value = "/{contractAddress}", method = RequestMethod.POST)
    public @ResponseBody EthereumResponse<T> runContract(@PathVariable final String contractAddress, @RequestBody final SampleVisitor visitor) throws NoSuchContractMethod {
        return new EthereumResponse(facade.runContract(contractAddress, visitor),200, "OK");
    }

}
