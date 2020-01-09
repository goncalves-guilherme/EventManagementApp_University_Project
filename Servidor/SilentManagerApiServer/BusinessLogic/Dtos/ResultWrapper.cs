using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using SilentManager.API.Models.DB;

namespace SilentManagerApiServer.BusinessLogic.Dtos
{
    public class ResultWrapper<T>
    {

        private IEnumerable<ErrorResult> _errors;
        private T _result;

        public IEnumerable<ErrorResult> Errors { get => _errors; }
        public T Result { get => _result; }

        public ResultWrapper(T result, IEnumerable<ErrorResult> error)
        {
            this._result = result;
            this._errors = error;
        }
    }
}
